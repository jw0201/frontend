package dev

import common.ExecutionContexts
import java.io.File
import play.api.libs.MimeTypes
import play.api.mvc._
import play.api.libs.iteratee.Enumerator

object DevAssetsController extends Controller with ExecutionContexts {

  def at(path: String): Action[AnyContent] = Action {
    val contentType: Option[String] = MimeTypes.forFileName(path) map { mime =>
      // Add charset for text types
      if (MimeTypes.isText(mime)) s"${mime}; charset=utf-8" else mime
    }

    val sourceFile = new File(s"static/src/$path")

    val resolved = if (sourceFile.exists()) {
      sourceFile.toURI.toURL
    } else {
      new File(s"static/hash/$path").toURI.toURL
    }

    Result(
      ResponseHeader(OK, Map(CONTENT_TYPE -> contentType.getOrElse(BINARY))),
      Enumerator.fromStream(resolved.openStream())
    )
  }

  def atFonts(path: String) = at(s"fonts/$path")
  def atJavascripts(file: String) = at(s"javascripts/$file")
  def atStylesheets(file: String) = at(s"stylesheets/$file")

  def humans(): Action[AnyContent] = controllers.Assets.at(path = "/public", file = "humans.txt")

  def surveys(file: String): Action[AnyContent] =
    controllers.Assets.at(path = "/public/surveys", file, aggressiveCaching = false)
}
