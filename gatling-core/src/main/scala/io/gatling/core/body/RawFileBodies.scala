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

import io.gatling.commons.validation.Validation
import io.gatling.core.session.{ Expression, StaticValueExpression }
import io.gatling.core.util.{ Resource, ResourceCache }
import io.gatling.core.util.cache.Cache

import com.github.benmanes.caffeine.cache.LoadingCache

final case class ResourceAndCachedBytes(resource: Resource, cachedBytes: Option[Array[Byte]])

final class RawFileBodies(cacheMaxCapacity: Long) extends ResourceCache {
  private val bytesCache: LoadingCache[Resource, Option[Array[Byte]]] = {
    val resourceToBytes: Resource => Option[Array[Byte]] = resource =>
      if (resource.file.length > cacheMaxCapacity) {
        None
      } else {
        Some(resource.bytes)
      }

    Cache.newConcurrentLoadingCache(cacheMaxCapacity, resourceToBytes)
  }

  private def asResourceAndCachedBytes(path: String): Validation[ResourceAndCachedBytes] =
    for {
      resource <- cachedResource(path)
    } yield ResourceAndCachedBytes(resource, bytesCache.get(resource))

  def asResourceAndCachedBytes(filePath: Expression[String]): Expression[ResourceAndCachedBytes] =
    filePath match {
      case StaticValueExpression(path) =>
        val staticResourceAndCachedBytes = asResourceAndCachedBytes(path)
        _ => staticResourceAndCachedBytes

      case _ =>
        filePath(_).flatMap(asResourceAndCachedBytes)
    }
}
