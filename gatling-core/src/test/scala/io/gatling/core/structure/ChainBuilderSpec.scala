/*
 * Copyright 2011-2025 GatlingCorp (https://gatling.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.gatling.core.structure

import io.gatling.commons.stats.{ KO, OK }
import io.gatling.commons.validation._
import io.gatling.core.{ CoreDsl, EmptySession }
import io.gatling.core.config.GatlingConfiguration
import io.gatling.core.session.Session
import io.gatling.core.stats.LoggingStatsEngine
import io.gatling.core.test._

class ChainBuilderSpec extends ScenarioTestFixture with CoreDsl with EmptySession {
  implicit val configuration: GatlingConfiguration = GatlingConfiguration.loadForTest()

  "exec" should "wrap Scenarios in chains" in {
    scenarioTest { implicit ctx =>
      val message1 = "Message 1"
      val message2 = "Message 2"
      val message3 = "Message 3"

      val chain = buildChain {
        exec { session =>
          logMsg(message1)
          session
        }.exec {
          scenario("Wrapped Scenario")
            .exec { session =>
              logMsg(message2)
              session
            }
        }.exec { session =>
          logMsg(message3)
          session
        }
      }

      chain ! emptySession

      expectMsg(message1)
      expectMsg(message2)
      expectMsg(message3)
    }
  }

  it should "fail group when action fails, eg when request can't be built" in {
    scenarioTest { implicit ctx =>
      val chain = buildChain {
        group("group") {
          exec(_ => "Forced failure".failure)
            .exec { session =>
              logMsg(session)
              session
            }
        }
      }

      chain ! emptySession

      expectMsgPF { case session: Session =>
        session.status shouldBe KO
      }

      expectMsgPF { case LoggingStatsEngine.Message.LogGroupEnd(_, group, _) =>
        group.status shouldBe KO
      }
    }
  }

  "tryMax" should "exit wrapped loop" in {
    scenarioTest { implicit ctx =>
      def message(action: Int, i: Int, j: Int) =
        s"action$action|$i|$j"

      def i(session: Session) =
        session("i").as[Int]

      def j(session: Session) =
        session("j").as[Int]

      val outerGroup = "outerGroup"
      val innerGroup = "innerGroup"

      val chain = buildChain {
        tryMax(2, "i") {
          repeat(2, "j") {
            group(outerGroup) {
              exec { session =>
                logMsg(message(1, i(session), j(session)))
                session
              }.group(innerGroup) {
                exec { session =>
                  if (i(session) == 0) {
                    "Forced failure".failure
                  } else {
                    session
                  }
                }.exec { session =>
                  logMsg(message(2, i(session), j(session)))
                  session
                }
              }
            }
          }
        }
      }

      chain ! emptySession
      expectMsg(message(1, 0, 0))
      expectMsgPF { case LoggingStatsEngine.Message.LogGroupEnd(_, group, _) =>
        group.groups shouldBe List(outerGroup, innerGroup)
        group.status shouldBe KO
      }
      expectMsgPF { case LoggingStatsEngine.Message.LogGroupEnd(_, group, _) =>
        group.groups shouldBe List(outerGroup)
        group.status shouldBe KO
      }
      expectMsg(message(1, 1, 0))
      expectMsg(message(2, 1, 0))
      expectMsgPF { case LoggingStatsEngine.Message.LogGroupEnd(_, group, _) =>
        group.groups shouldBe List(outerGroup, innerGroup)
        group.status shouldBe OK
      }
      expectMsgPF { case LoggingStatsEngine.Message.LogGroupEnd(_, group, _) =>
        group.groups shouldBe List(outerGroup)
        group.status shouldBe OK
      }
      expectMsg(message(1, 1, 1))
      expectMsg(message(2, 1, 1))
      expectMsgPF { case LoggingStatsEngine.Message.LogGroupEnd(_, group, _) =>
        group.groups shouldBe List(outerGroup, innerGroup)
        group.status shouldBe OK
      }
      expectMsgPF { case LoggingStatsEngine.Message.LogGroupEnd(_, group, _) =>
        group.groups shouldBe List(outerGroup)
        group.status shouldBe OK
      }
    }
  }
}
