package com.tipsi.cameraroll;

import android.graphics.Bitmap;

import java.util.Map;

class ParsedDataUrl {

  static final String MIME_JPEG = "image/jpeg";
  static final String MIME_PNG = "image/png";

  final Map<String, Bitmap.CompressFormat> typeToCompressFormat;

  final String data;
  final String mimeType;

  ParsedDataUrl(String url, Map<String, Bitmap.CompressFormat> typeToCompressFormat) throws Throwable{
    this.typeToCompressFormat = typeToCompressFormat;

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

  Bitmap.CompressFormat getCompressFormat() {
    return typeToCompressFormat.get(mimeType.substring("image/".length()));
  }
}
