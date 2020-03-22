package org.scalablytyped.converter.internal.importer

import java.net.URI

import org.scalablytyped.converter.internal.InFolder

import scala.util.Try

object DTUpToDate {
  def apply(cmd: Cmd, offline: Boolean, cacheFolder: os.Path, repo: URI): InFolder = {
    val clonedDir: os.Path = cacheFolder / 'DefinitelyTyped

    Try(
      if (os.exists(clonedDir)) {
        if (!offline) {
          implicit val wd = clonedDir
          cmd.runVerbose git 'fetch
          cmd.runVerbose git ("clean", "-fdX") // remove ignored files/folders
          cmd.runVerbose git ("clean", "-fd")
          cmd.runVerbose git ('reset, "--hard", "origin/master")
          cmd.rmVerbose  (clonedDir / ".git/gc.log")
          cmd.runVerbose git 'prune
        }
      } else
        cmd.runVerbose("git", "clone", repo.toString)(cacheFolder),
    )

    // use first party definitions instead. model better if there are more cases like this
    os.remove.all(clonedDir / "types/highcharts")
    os.remove.all(clonedDir / "types/expo")

    InFolder(clonedDir / 'types)
  }
}
