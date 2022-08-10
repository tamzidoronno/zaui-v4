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
  let suffix = "";

  if (hotelCode.endsWith(ClusterSuffix.Cluster9)) {
    gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Cluster9];
    suffix = ClusterSuffix.Cluster9;
  } else if (hotelCode.endsWith(ClusterSuffix.Cluster4)) {
    gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Cluster4];
    suffix = ClusterSuffix.Cluster4;
  } else if (hotelCode.endsWith(ClusterSuffix.Cluster6)) {
    gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Cluster6];
    suffix = ClusterSuffix.Cluster6;
  } else if (hotelCode.endsWith(ClusterSuffix.Qp)) {
    gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Qp];
    suffix = ClusterSuffix.Qp;
  } else if (hotelCode.endsWith(ClusterSuffix.Local)) {
    gluewareUrl = GLUEWARE_URL_LIST[ClusterSuffix.Local];
    suffix = ClusterSuffix.Local;
  }

  return gluewareUrl;
}
const ClusterSuffix = {
  Cluster9: "c9",
  Cluster4: "c4",
  Cluster6: "c6",
  Qp: "qp",
  Local: "local",
};

const GLUEWARE_URL_LIST = {
  [ClusterSuffix.Cluster9]: "https://gluegc9.getshop.com/",
  [ClusterSuffix.Cluster6]: "https://glueware4.zauistay.com/",
  [ClusterSuffix.Cluster4]: "https://glueware6.zauistay.com/",
  [ClusterSuffix.Qp]: "https://glue.mdev.getshop.com/",
  [ClusterSuffix.Local]: "http://localhost:3000/",
};