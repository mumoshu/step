package com.thinkminimo.step

import org.scalatest.matchers.ShouldMatchers

class StepSuiteTestServlet extends Step {
  before {
    contentType = "text/html; charset=utf-8"
    characterEncoding = "utf-8"
  }

  get("/") {
    "root"
  }

  get("/session") {
    session("name") match {
      case Some(n: String) => n
      case _ => "error!"
    }
  }

  post("/session") {
    session("name") = params("name")
    session("name") match {
      case Some(n: String) => n
      case _ => "error!"
    }
  }

  get("/redirect") {
    redirect("/redirected")
  }

  get("/echo_params") {
    params("msg")
  }

  post("/echo_params") {
    params("msg")
  }
}

class StepSuiteTest extends StepSuite with ShouldMatchers {
  route(classOf[StepSuiteTestServlet], "/*")

  test("route test") {
    get("/") {
      status should equal (200)
      body should include ("root")
    }
  }

  test("get test") {
    get("/echo_params", "msg" -> "hi") {
      status should equal (200)
      body should equal ("hi")
    }
  }
  test("get with multi-byte chars in params") {
    // `msg` will automatically be url-encoded by StepSuite
    get("/echo_params", "msg" -> "こんにちわ") {
      status should equal (200)
      body should equal ("こんにちわ")
    }
  }

  test("post test") {
    post("/echo_params", "msg" -> "hi") {
      status should equal (200)
      body should equal ("hi")
    }
  }

  test("post multi-byte chars test") {
    // `msg` will automatically be url-encoded by StepSuite
    post("/echo_params", "msg" -> "こんにちわ") {
      status should equal (200)
      body should equal ("こんにちわ")
    }
  }

  test("header test") {
    get("/redirect") {
      status should equal (302)
      header("Location") should include ("/redirected")
    }
  }

  test("abbrevs test") {
    get("/redirect") {
      status should equal (response status)
      body should equal (response body)
      header("Location") should equal (response header("Location"))
    }
  }

  test("session test") {
    val name = "Step"
    post("/session", "name" -> name) {
      status should equal (200)
      body should include (name)
    }
    get("/session") {
      status should equal (200)
      body should not include (name)
    }
    session {
      post("/session", "name" -> name) {
	status should equal (200)
	body should include (name)
      }
      get("/session") {
	status should equal (200)
	body should include (name)
      }
    }
  }

  // @todo put test

  // @todo delete test
}
