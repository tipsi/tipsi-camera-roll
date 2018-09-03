package com.tipsi.cameraroll;

class ParsedDataUrl {

  static final String MIME_JPEG = "image/jpeg";
  static final String MIME_PNG = "image/png";

  final String data;
  final String mimeType;

  ParsedDataUrl(String url) throws Throwable{
    ArgChecks.requireNonNull(url, "url");

    String[] chunks = url.split(",");

    ArgChecks.require(chunks.length == 2, "url required shape: header,data");

    String header = chunks[0];
    data = chunks[1];

    chunks = header.split("(:|;)");

    ArgChecks.require(chunks.length >= 3 , "header required shape: data:mime;base64");

    String mimeChunk = chunks[1];

    ArgChecks.require(
      MIME_PNG.equals(mimeChunk) || MIME_JPEG.equals(mimeChunk) ,
      "image/jpeg or image/png"
    );

    mimeType = mimeChunk;
  }

  String getFileExtension() {
    return mimeType.split("/")[1];
  }
}
