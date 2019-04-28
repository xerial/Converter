package com.olvind.tso
import java.net.URLEncoder

import scala.annotation.switch

object stringUtils {
  val Quote = '"'

  def quote(s: String): String =
    s"$Quote$s$Quote"

  def unquote(s: String): String =
    s.replaceAllLiterally(Quote.toString, "")

  /**
    * pff, scala apparently cares about nested comments, especially when they're not balanced
    */
  def escapeNestedComments(s: String): String =
    (s.indexOf("/*"), s.lastIndexOf("*/")) match {
      case (-1, _) => s
      case (_, -1) => s
      case (start, end) =>
        val `/*` = s.substring(0, start + 2)
        val `*/` = s.substring(end)
        val escaped =
          s.substring(start + 2, end).replaceAll("/\\*", "/ *").replaceAll("\\*/", "* /")
        `/*` + escaped + `*/`
    }

  def formatComment(s: String): String = {
    val sb  = new StringBuilder
    var idx = 0

    while (idx < s.length) {
      (s(idx): @switch) match {
        case '\n' =>
          /* don't output consecutive newlines */
          if (sb.isEmpty || sb(sb.length - 1) =/= '\n') {
            sb.append('\n')
          }

          /* replace spaces (if any) after newline with exactly two spaces */
          var hasStrippedSpace = false
          idx += 1
          while (idx < s.length && s(idx) === ' ') {
            hasStrippedSpace = true
            idx += 1
          }
          if (hasStrippedSpace) {
            sb.append("  ")
          }

        case other =>
          sb.append(other)
          idx += 1
      }
    }

    /* if the comment doesn't end with a newline, add a singular space */
    if (sb.endsWith("*/")) {
      sb.append(" ")
    }

    sb.toString()
  }

  /**
    * Apparently scala cares and typescript doesn't
    */
  def escapeUnicodeEscapes(s: String): String =
    s.replaceAll("\\\\u", "\\\\\\\\u")

  def unCapitalize(str: String): String =
    if (str.length == 0) ""
    else if (str.charAt(0).isLower) str
    else {
      val chars = str.toCharArray
      chars(0) = chars(0).toLower
      new String(chars)
    }

  // https://stackoverflow.com/a/611117
  def encodeURIComponent(s: String): String =
    URLEncoder
      .encode(s, "UTF-8")
      .replaceAll("\\+", "%20")
      .replaceAll("\\%21", "!")
      .replaceAll("\\%27", "'")
      .replaceAll("\\%28", "(")
      .replaceAll("\\%29", ")")
      .replaceAll("\\%7E", "~")

}
