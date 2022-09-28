(function ($) {
    $.fn.getshopbooking = function (options) {
      old_url = options.jsendpoint;
      let splitted = old_url.split(".");
      let hotelCode = splitted[0].replace("https://", "");
      let glueware_url = getGluewareUrl(hotelCode);
      options.webaddress = options.endpoint;
      options.endpoint = glueware_url;
      $.getScript(`${glueware_url}/js/getshop.bookingembed.js`, function () {
        $("#bookingprocess").getshopbooking(options);
      });
    };
  })(jQuery);

  function getGluewareUrl(hotelCode) {
    let gluewareUrl = "";
    if (hotelCode.endsWith(ClusterSuffix.Cluster9)) {
      gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Cluster9];
    } else if (hotelCode.endsWith(ClusterSuffix.Cluster5)) {
      gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Cluster5];
    }
    else if (hotelCode.endsWith(ClusterSuffix.Cluster6)) {
      gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Cluster6];
    }
    else if (hotelCode.endsWith(ClusterSuffix.Cluster41)) {
      gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Cluster41];
    }
    else if (hotelCode.endsWith(ClusterSuffix.Qp)) {
      gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Qp];
    }
    else if (hotelCode.endsWith(ClusterSuffix.Local)) {
      gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Local];
    }
    else {
    // as all named domain hotels reside at c4
      gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Cluster4];
    }

    return gluewareUrl;
  }
  const ClusterSuffix = {
    Cluster9: "c9",
    Cluster4: "c4",
    Cluster5: "c5",
    Cluster6: "c6",
    Cluster41: "c41",
    Qp: "mdev",
    Local: "local",
  };

  const GLUEWARE_URL_LIST = {
    [ClusterSuffix.Cluster9]: "https://gluegc9.getshop.com/",
    [ClusterSuffix.Cluster6]: "https://glueware6.zauistay.com/",
    [ClusterSuffix.Cluster4]: "https://glueware4.zauistay.com/",
    [ClusterSuffix.Cluster5]: "https://glueware5.zauistay.com/",
    [ClusterSuffix.Cluster41]: "https://glueware41.zauistay.com/",
    [ClusterSuffix.Qp]: "https://glue.mdev.getshop.com/",
    [ClusterSuffix.Local]: "http://localhost:3000/",
  };