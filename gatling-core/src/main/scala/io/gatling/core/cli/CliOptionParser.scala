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

package io.gatling.core.cli

import io.gatling.shared.cli.CliOption

import scopt.{ OptionDef, OptionParser, Read }

private[gatling] class CliOptionParser[B](programName: String) extends OptionParser[B](programName) {

  def opt[A: Read](constant: CliOption): OptionDef[A, B] = {
    val base = opt[A](constant.longName).abbr(constant.shortName).text(constant.text)
    if (constant.valueName == null) {
      base
    } else {
      base.valueName(constant.valueName)
    }
  }

  override def errorOnUnknownArgument: Boolean = false
}
