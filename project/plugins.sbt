ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always

addSbtPlugin("io.gatling"         % "gatling-build-plugin"  % "6.3.0")
addSbtPlugin("com.github.sbt"     % "sbt-native-packager"   % "1.11.1")
addSbtPlugin("pl.project13.scala" % "sbt-jmh"               % "0.4.7")
addSbtPlugin("net.aichler"        % "sbt-jupiter-interface" % "0.11.1")
addSbtPlugin("org.wartremover"    % "sbt-wartremover"       % "3.3.0")
addSbtPlugin("ch.epfl.scala"      % "sbt-scalafix"          % "0.14.2")
addSbtPlugin("net.moznion.sbt"    % "sbt-spotless"          % "0.1.3")
