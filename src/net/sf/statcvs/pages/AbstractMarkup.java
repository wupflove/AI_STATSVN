package net.sf.statcvs.pages;


public abstract class AbstractMarkup {
    protected void addTwitterScript(final StringBuffer b) {
//        if ("xdoc".equalsIgnoreCase(ConfigurationOptions.getOutputFormat())) {
//            b.append("<![CDATA[").append("\n");
//        }
        b.append("<script type=\"text/javascript\" charset=\"utf-8\" src=\"");
        b.append("http://bit.ly/javascript-api.js?version=latest&#38;login=statsvn&#38;apiKey=R_2c362b417d0517c27876cbaca1bb68cc");
        b.append("\"></script>\n");
//        if ("xdoc".equalsIgnoreCase(ConfigurationOptions.getOutputFormat())) {
//            b.append("]]>");
//        }
        b.append("<script type=\"text/javascript\" charset=\"utf-8\">").append("\n");
        b.append("   // wait until page is loaded to call API").append("\n");
        b.append("   var redir_url;").append("\n");
        b.append("   var short_url;").append("\n");
        b.append("   BitlyCB.myShortenCallback = function(data) {").append("\n");
        b.append("      // this is how to get a result of shortening a single url").append("\n");
        b.append("      var result;").append("\n");
        b.append("      short_url=null;").append("\n");
        b.append("      for (var r in data.results) {").append("\n");
        b.append("         result = data.results[r];").append("\n");
        b.append("         result['longUrl'] = r;").append("\n");
        b.append("         short_url = result['shortUrl'];").append("\n");
        b.append("         break;").append("\n");
        b.append("      }").append("\n");
        b.append("      if (short_url==null) // bit.ly failed").append("\n");
        b.append("      document.location=redir_url;").append("\n");
        b.append("      else").append("\n");
        b.append("      redirToTwitter();").append("\n");
        b.append("   };").append("\n");
        b.append("   function shortenTweet(url) {").append("\n");
        b.append("      if (short_url == null || redir_url!=url) {").append("\n");
        b.append("      redir_url = url;").append("\n");
        b.append("      BitlyClient.shorten(document.location, 'BitlyCB.myShortenCallback');").append("\n");
        b.append("      }").append("\n");
        b.append("      else").append("\n");
        b.append("      redirToTwitter();").append("\n");
        b.append("      return false;").append("\n");
        b.append("   }").append("\n");
        b.append("   function redirToTwitter() { ").append("\n");
        b.append("      // replace {0} which is visible as %7B0} in the link").append("\n");
        b.append("      document.location = redir_url.replace('%7B0}', short_url);").append("\n");
        b.append("   }").append("\n");
        b.append("</script>").append("\n");

        /*
        b.append("var redir_url;").append("\n");
        b.append("BitlyCB.myShortenCallback = function(data) {").append("\n");
        b.append("var result;").append("\n");
        b.append("for (var r in data.results) {").append("\n");
        b.append("result = data.results[r];").append("\n");
        b.append("result['longUrl'] = r;").append("\n");
        b.append("break;").append("\n");
        b.append("}").append("\n");
        b.append("document.location = redir_url.replace('{0}', result['shortUrl']);").append("\n");
        b.append("};").append("\n");
        b.append("function shortenTweet(url) {").append("\n");
        b.append("redir_url = url;").append("\n");
        b.append("BitlyClient.shorten(url, 'BitlyCB.myShortenCallback');").append("\n");
        b.append("return false;").append("\n");
        b.append("}").append("\n");
        b.append("</script>").append("\n");
        */
    }
}
