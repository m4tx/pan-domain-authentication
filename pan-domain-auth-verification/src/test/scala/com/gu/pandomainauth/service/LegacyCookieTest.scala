package com.gu.pandomainauth.service

import java.util.Date

import com.gu.pandomainauth.model.{CookieSignatureInvalidException, CookieParseException, User, AuthenticatedUser}
import org.scalatest.{Matchers, FreeSpec}

class LegacyCookieTest extends FreeSpec with Matchers {
  val authUser = AuthenticatedUser(User("test", "user", "test.user@example.com", None), "testsuite", Set("testsuite", "another"), new Date().getTime + 86400, multiFactor = true)
  val data = CookieUtils.serializeAuthenticatedUser(authUser)
  val secret = "testSecret"

  "generate/parse round trip restores a valid authedUser" in {
    LegacyCookie.parseCookieData(LegacyCookie.generateCookieData(authUser, secret), secret) should equal(authUser)
  }

  "generateCookieData should create a cookie value with the correct structure" in {
    LegacyCookie.generateCookieData(authUser, secret) should fullyMatch regex "^^([\\w\\W]*)>>([\\w\\W]*)$".r
  }

  "parseCookieData" - {
    "throws a CookieParseException when given invalid data" in {
      intercept[CookieParseException] {
        LegacyCookie.parseCookieData("bad cookie data", secret)
      }
    }

    "throws a CookieParseException if the signature is invalid" in {
      intercept[CookieSignatureInvalidException] {
        LegacyCookie.parseCookieData("data>>invalid-signature", secret)
      }
    }
  }
}
