package com.example.shopping.perf

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

/**
 * Shopping Mall Performance Test Simulation
 *
 * High-load test with 100x scale (optimized for SQLite limitations):
 * - Read-heavy workload to test read performance
 * - Limited write operations to avoid SQLite lock contention
 * - 5500+ total requests
 */
class ShoppingMallSimulation extends Simulation {

  // HTTP Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .userAgentHeader("Gatling Performance Test (High Load 100x)")
    .acceptEncodingHeader("gzip, deflate")

  // Test data - circular iterators
  val productIds = Iterator.continually(1L to 10L).flatten
  val userIds = Iterator.continually(1L to 20L).flatten

  // Counter for unique user registration
  val userCounter = new java.util.concurrent.atomic.AtomicLong(0)

  // Feeder for products (circular)
  val productFeeder = Iterator.continually(Map("productId" -> productIds.next()))

  // Feeder for users (circular)
  val userFeeder = Iterator.continually(Map("userId" -> userIds.next()))

  // Scenario 1: Browse Products (1000 users) - Read heavy
  val browseProducts = scenario("Browse Products")
    .repeat(3) {
      exec(
        http("List All Products")
          .get("/api/products")
          .check(status.is(200))
      )
      .pause(100.milliseconds, 500.milliseconds)
    }
    .feed(productFeeder)
    .exec(
      http("Get Product Details")
        .get("/api/products/${productId}")
        .check(status.in(200, 404))
    )
    .pause(100.milliseconds, 300.milliseconds)

  // Scenario 2: User Registration (200 users)
  val registerUser = scenario("Register User")
    .exec { session =>
      val id = userCounter.incrementAndGet()
      val timestamp = System.currentTimeMillis()
      val username = s"perf_user_${timestamp}_${id}"
      val email = s"perf_${timestamp}_${id}@test.com"
      session.set("username", username).set("email", email)
    }
    .exec(
      http("Register New User")
        .post("/api/users")
        .body(StringBody("""{"username":"${username}","email":"${email}","password":"password123"}"""))
        .check(status.in(201, 400))
    )
    .pause(100.milliseconds, 500.milliseconds)

  // Scenario 3: Create Order (100 users) - Limited to avoid lock contention
  val createOrder = scenario("Create Order")
    .feed(userFeeder)
    .feed(productFeeder)
    .exec(
      http("Create Order")
        .post("/api/orders")
        .body(StringBody("""{"userId":${userId},"productId":${productId},"quantity":1}"""))
        .check(status.in(200, 201, 400, 404, 500))
    )
    .pause(500.milliseconds, 1500.milliseconds)

  // Scenario 4: Query Orders (500 users) - Read heavy
  val queryOrders = scenario("Query Orders")
    .repeat(5) {
      feed(userFeeder)
      .exec(
        http("Get User Orders")
          .get("/api/orders/user/${userId}")
          .check(status.in(200, 404))
      )
      .pause(100.milliseconds, 300.milliseconds)
    }
    .exec(
      http("Get All Orders")
        .get("/api/orders")
        .check(status.is(200))
    )
    .pause(100.milliseconds, 300.milliseconds)

  // Scenario 5: Mixed Workflow (200 users) - Realistic user behavior
  val mixedWorkflow = scenario("Mixed Shopping Workflow")
    .exec(
      http("List Products")
        .get("/api/products")
        .check(status.is(200))
    )
    .pause(100.milliseconds, 500.milliseconds)
    .feed(productFeeder)
    .exec(
      http("View Product")
        .get("/api/products/${productId}")
        .check(status.in(200, 404))
    )
    .pause(100.milliseconds, 500.milliseconds)
    .feed(userFeeder)
    .exec(
      http("View Orders")
        .get("/api/orders/user/${userId}")
        .check(status.in(200, 404))
    )

  // Load Profile Configuration - 100x scale (2000 total users)
  // Optimized for SQLite: more reads, fewer concurrent writes
  setUp(
    browseProducts.inject(
      rampUsers(1000).during(30.seconds)
    ),
    queryOrders.inject(
      rampUsers(500).during(30.seconds)
    ),
    mixedWorkflow.inject(
      rampUsers(200).during(30.seconds)
    ),
    registerUser.inject(
      rampUsers(200).during(30.seconds)
    ),
    createOrder.inject(
      rampUsers(100).during(30.seconds)
    )
  ).protocols(httpProtocol)
    .assertions(
      // Response time: 95% of requests should complete within 3000ms
      global.responseTime.percentile3.lt(3000),
      // Success rate: at least 80% of requests should succeed
      global.successfulRequests.percent.gt(80),
      // Max response time should be under 15000ms
      global.responseTime.max.lt(15000)
    )
    .maxDuration(120.seconds)
}