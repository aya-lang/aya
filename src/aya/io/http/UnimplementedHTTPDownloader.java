package aya.io.http;

import java.io.IOException;

import aya.exceptions.runtime.IOError;

public class UnimplementedHTTPDownloader extends AbstractHTTPDownloader {

	@Override
	public String downloadFile(String url) throws IOException {
		throw new IOError("", "UnimplementedHTTPDownloader.downloadFile", "downloadFile unimplemented");
	}

}
