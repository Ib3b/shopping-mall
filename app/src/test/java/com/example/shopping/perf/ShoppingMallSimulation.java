package com.example.shopping.perf;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import java.util.concurrent.atomic.AtomicLong;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static java.time.Duration.*;

/**
 * Gatling performance test covering all write endpoints.
 * 8 scenarios, 2200 total users, 3:1 read:write ratio.
 */
public class ShoppingMallSimulation extends Simulation {

    // Circular feeders (thread-safe, no Gatling dependency)
    private static final AtomicLong COUNTER = new AtomicLong(0);
    private static final long[] UIDS = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
    private static final long[] PIDS = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,
        21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50};
    private static final long[] SIDS = {4,10,16,17,18,41,54,58,59,60};
    private static final AtomicLong UIDX = new AtomicLong(0);
    private static final AtomicLong PIDX = new AtomicLong(0);
    private static final AtomicLong SIDX = new AtomicLong(0);

    private static long next(long[] arr, AtomicLong idx) {
        return arr[(int)(idx.getAndIncrement() % arr.length)];
    }

    private static String unique(String pfx) {
        return pfx + "_" + System.currentTimeMillis() + "_" + COUNTER.incrementAndGet();
    }

    // ------------------------------------------------------------------
    // HTTP protocol
    // ------------------------------------------------------------------

    HttpProtocolBuilder http_ = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .userAgentHeader("Gatling Write Coverage Test")
        .acceptEncodingHeader("gzip, deflate");

    // ------------------------------------------------------------------
    // 1 — Browse Products  (1000 read-only users)
    // ------------------------------------------------------------------

    ScenarioBuilder browseProducts = scenario("Browse Products")
        .repeat(3).on(
            exec(http("List Products").get("/api/products").check(status().is(200)))
                .pause(ofMillis(100), ofMillis(500)))
        .exec(session -> session.set("pid", next(PIDS, PIDX)))
        .exec(http("Get Product").get("/api/products/#{pid}").check(status().in(200, 404)))
        .pause(ofMillis(100), ofMillis(300));

    // ------------------------------------------------------------------
    // 2 — Register User  (200 write users)
    // ------------------------------------------------------------------

    ScenarioBuilder registerUser = scenario("Register User")
        .exec(session -> session
            .set("ru", unique("perf_user"))
            .set("re", unique("perf") + "@test.com"))
        .exec(http("Register")
            .post("/api/users")
            .body(StringBody("{\"username\":\"#{ru}\",\"email\":\"#{re}\",\"password\":\"password123\"}"))
            .check(status().in(201, 400)))
        .pause(ofMillis(100), ofMillis(500));

    // ------------------------------------------------------------------
    // 3 — User Profile Update  (100 write users)  POST→PUT→GET
    // ------------------------------------------------------------------

    ScenarioBuilder userProfile = scenario("User Profile Update")
        .exec(session -> session
            .set("nu", unique("profile")).set("ne", unique("prof") + "@test.com"))
        .exec(http("Create for Update")
            .post("/api/users")
            .body(StringBody("{\"username\":\"#{nu}\",\"email\":\"#{ne}\",\"password\":\"p\"}"))
            .check(status().is(201), jsonPath("$.id").saveAs("uid")))
        .pause(ofMillis(200), ofMillis(500))
        .exec(session -> session
            .set("nu2", unique("profile2")).set("ne2", unique("prof2") + "@test.com"))
        .exec(http("Update User")
            .put("/api/users/#{uid}")
            .body(StringBody("{\"username\":\"#{nu2}\",\"email\":\"#{ne2}\",\"password\":\"p2\"}"))
            .check(status().is(200), jsonPath("$.username").is("#{nu2}")))
        .pause(ofMillis(200), ofMillis(500))
        .exec(http("Verify Update")
            .get("/api/users/#{uid}")
            .check(status().is(200), jsonPath("$.username").is("#{nu2}")));

    // ------------------------------------------------------------------
    // 4 — Create Order  (100 write users, optimistic-lock contention)
    // ------------------------------------------------------------------

    ScenarioBuilder createOrder = scenario("Create Order")
        .exec(session -> session
            .set("uid2", next(UIDS, UIDX))
            .set("pid2", next(PIDS, PIDX)))
        .exec(http("Create Order")
            .post("/api/orders")
            .body(StringBody("{\"userId\":#{uid2},\"productId\":#{pid2},\"quantity\":1}"))
            .check(status().in(200, 201, 400, 404, 500)))
        .pause(ofMillis(500), ofMillis(1500));

    // ------------------------------------------------------------------
    // 5 — Order Lifecycle  (50 write-intensive users)
    //     POST→PAID→SHIPPED→DELIVERED→CANCEL(400)
    // ------------------------------------------------------------------

    ScenarioBuilder orderLifecycle = scenario("Order Lifecycle")
        .exec(session -> session
            .set("uid3", next(UIDS, UIDX))
            .set("sid", next(SIDS, SIDX)))
        .exec(http("Create for Lifecycle")
            .post("/api/orders")
            .body(StringBody("{\"userId\":#{uid3},\"productId\":#{sid},\"quantity\":1}"))
            .check(status().in(201, 200), jsonPath("$.id").saveAs("oid")))
        .pause(ofMillis(300), ofMillis(800))
        .exec(http("Pay").put("/api/orders/#{oid}/status?status=PAID")
            .check(status().in(200, 400)))
        .pause(ofMillis(300), ofMillis(800))
        .exec(http("Ship").put("/api/orders/#{oid}/status?status=SHIPPED")
            .check(status().in(200, 400)))
        .pause(ofMillis(300), ofMillis(800))
        .exec(http("Deliver").put("/api/orders/#{oid}/status?status=DELIVERED")
            .check(status().in(200, 400)))
        .pause(ofMillis(300), ofMillis(800))
        .exec(http("Cancel (expect 400)")
            .post("/api/orders/#{oid}/cancel").check(status().is(400)));

    // ------------------------------------------------------------------
    // 6 — Query Orders  (500 read-only users)
    // ------------------------------------------------------------------

    ScenarioBuilder queryOrders = scenario("Query Orders")
        .repeat(5).on(
            exec(session -> session.set("uid4", next(UIDS, UIDX)))
                .exec(http("User Orders").get("/api/orders/user/#{uid4}")
                    .check(status().in(200, 404)))
                .pause(ofMillis(100), ofMillis(300)))
        .exec(http("All Orders").get("/api/orders").check(status().is(200)))
        .pause(ofMillis(100), ofMillis(300));

    // ------------------------------------------------------------------
    // 7 — Product CRUD  (50 write-intensive users)
    //     POST→GET→PUT→GET→DELETE
    // ------------------------------------------------------------------

    ScenarioBuilder productCrud = scenario("Product CRUD")
        .exec(session -> {
            String name = unique("gatling-test");
            return session
                .set("cn", name)
                .set("cn2", name.replace("gatling-test", "gatling-upd"));
        })
        .exec(http("Create Product")
            .post("/api/products")
            .body(StringBody("{\"name\":\"#{cn}\",\"description\":\"perf\",\"price\":99.99,\"stock\":100,\"category\":\"perf-test\"}"))
            .check(status().is(201), jsonPath("$.id").saveAs("cid")))
        .pause(ofMillis(500), ofMillis(1000))
        .exec(http("Get Product").get("/api/products/#{cid}")
            .check(status().is(200), jsonPath("$.name").is("#{cn}")))
        .pause(ofMillis(500), ofMillis(1000))
        .exec(http("Update Product")
            .put("/api/products/#{cid}")
            .body(StringBody("{\"name\":\"#{cn2}\",\"description\":\"upd\",\"price\":199.99,\"stock\":50,\"category\":\"perf-test\"}"))
            .check(status().is(200), jsonPath("$.name").is("#{cn2}")))
        .pause(ofMillis(500), ofMillis(1000))
        .exec(http("Verify Cache").get("/api/products/#{cid}")
            .check(status().is(200), jsonPath("$.name").is("#{cn2}"), jsonPath("$.price").is("199.99")))
        .pause(ofMillis(500), ofMillis(1000))
        .exec(http("Delete Product").delete("/api/products/#{cid}")
            .check(status().is(204)));

    // ------------------------------------------------------------------
    // 8 — Mixed Workflow  (200 users, read + one write)
    // ------------------------------------------------------------------

    ScenarioBuilder mixedWorkflow = scenario("Mixed Workflow")
        .exec(http("List").get("/api/products").check(status().is(200)))
        .pause(ofMillis(100), ofMillis(500))
        .exec(session -> session.set("pid5", next(PIDS, PIDX)))
        .exec(http("View").get("/api/products/#{pid5}").check(status().in(200, 404)))
        .pause(ofMillis(100), ofMillis(500))
        .exec(session -> session.set("uid5", next(UIDS, UIDX)))
        .exec(http("Orders").get("/api/orders/user/#{uid5}").check(status().in(200, 404)))
        .pause(ofMillis(100), ofMillis(500))
        .exec(session -> session.set("pid6", next(PIDS, PIDX)))
        .exec(http("Write").post("/api/orders")
            .body(StringBody("{\"userId\":1,\"productId\":#{pid6},\"quantity\":1}"))
            .check(status().in(201, 200, 400)));

    // ------------------------------------------------------------------
    // Load profile
    // ------------------------------------------------------------------

    {
        setUp(
            browseProducts.injectOpen(rampUsers(1000).during(ofSeconds(30))),
            registerUser.injectOpen(rampUsers(200).during(ofSeconds(30))),
            userProfile.injectOpen(rampUsers(100).during(ofSeconds(20))),
            createOrder.injectOpen(rampUsers(100).during(ofSeconds(30))),
            orderLifecycle.injectOpen(rampUsers(50).during(ofSeconds(20))),
            queryOrders.injectOpen(rampUsers(500).during(ofSeconds(30))),
            productCrud.injectOpen(rampUsers(50).during(ofSeconds(20))),
            mixedWorkflow.injectOpen(rampUsers(200).during(ofSeconds(30)))
        ).protocols(http_)
            .assertions(
                global().responseTime().percentile3().lt(3000),
                global().successfulRequests().percent().gt(90.0),
                global().responseTime().max().lt(15000)
            )
            .maxDuration(ofSeconds(120));
    }
}
