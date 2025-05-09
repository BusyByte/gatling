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

package io.gatling.core.body

import scala.util.{ Success, Try }

import io.gatling.ValidationValues

import io.pebbletemplates.pebble.extension.AbstractExtension
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

object TestExtension extends AbstractExtension

class PebbleExtensionsSpec extends AnyFlatSpecLike with Matchers with ValidationValues {
  "PebbleExtensions" should "should allow registration" in {
    Try(PebbleExtensions.register(Seq(TestExtension))) shouldBe Success(())
  }
}
