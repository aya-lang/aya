package aya.io.http;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class HTTPDownloader extends AbstractHTTPDownloader {

	@Override
	public String downloadFile(String url_str) throws IOException {
		Scanner scanner = null;
		try {
			URL url = new URL(url_str);
			scanner = new Scanner(url.openStream());
			StringBuilder sb = new StringBuilder();
			while(scanner.hasNext()) {
				sb.append(scanner.nextLine()).append('\n');
			}
			return sb.toString();
		} catch (IOException e) {
			throw e;
		} finally {
			if (scanner != null) scanner.close();
		}
	}

}
