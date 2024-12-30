package aya.io.http;

import java.io.IOException;

public abstract class AbstractHTTPDownloader {
	public abstract String downloadFile(String url) throws IOException;
}
