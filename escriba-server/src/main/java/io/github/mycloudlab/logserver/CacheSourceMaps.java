package io.github.mycloudlab.logserver;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.google.debugging.sourcemap.SourceMapConsumerFactory;
import com.google.debugging.sourcemap.SourceMapParseException;
import com.google.debugging.sourcemap.SourceMapping;

import io.quarkus.cache.CacheResult;

@ApplicationScoped
public class CacheSourceMaps {

	private LogServerConfig config;

	@Inject
	public CacheSourceMaps(LogServerConfig config) {
		this.config = config;
	}

	@CacheResult(cacheName = "sourcemaps-cache")
	public SourceMap load(String urlJSMinified) {
		try {
			urlJSMinified = config.translate(urlJSMinified);

			String sourceMapFileName = getSourceMapFileNameFromJSMinifiedFile(urlJSMinified);
			String urlSourceMapFile = generateUrlSourceMapFromFileName(urlJSMinified, sourceMapFileName);
			String sourceMapContent = loadContentSourceMapFromUrl(urlSourceMapFile);
			SourceMap sourceMap = generateSourceMapFromContent(sourceMapContent);

			return sourceMap;
		} catch (Exception e) {
			// when error in phase, generate caching sourcemap temporary
			SourceMap sourceMap = new SourceMap();
			return sourceMap;
		}
	}

	private SourceMap generateSourceMapFromContent(String sourceMapContent) throws SourceMapParseException {
		SourceMapping sourceMapping = SourceMapConsumerFactory.parse(sourceMapContent);
		SourceMap sourceMap = new SourceMap(sourceMapping);
		return sourceMap;
	}

	private String loadContentSourceMapFromUrl(String urlSourceMapFile) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest request = new HttpGet(urlSourceMapFile);
		request = config.fillRequestExtraHeaders(request);
		HttpResponse response = client.execute(request);
		return EntityUtils.toString(response.getEntity());
	}

	private String generateUrlSourceMapFromFileName(String urlJSMinified, String sourceMapFileName)
			throws MalformedURLException {
		String fileNameJSMinified = Paths.get(urlJSMinified).getFileName().toString();
		String pathBase = urlJSMinified.split(fileNameJSMinified)[0];
		String urlSourceMapFile = new StringBuffer().append(pathBase).append(sourceMapFileName).toString();
		return urlSourceMapFile;
	}

	private String getSourceMapFileNameFromJSMinifiedFile(String urlJSMinified) throws Exception {
		HttpClient client = HttpClientBuilder.create().build();
		HttpUriRequest request = new HttpGet(urlJSMinified);
		HttpResponse response = client.execute(request);
		try (
				InputStream stream = response.getEntity().getContent();
				Scanner scanner = new Scanner(stream);//
		) {
			// search pattern
			Pattern pattern = Pattern.compile(".*//# sourceMappingURL=", Pattern.DOTALL);
			scanner.skip(pattern);
			String sourceMapFileName = scanner.next();
			return sourceMapFileName;
		}

	}

}