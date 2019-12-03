package com.example.demo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.Asset;
import com.example.demo.web.UpdateAssetRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class AssetClient {
	private String baseUrl;
	private RestTemplate restTemplate = new RestTemplate();

	public AssetClient(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void deleteAllAssets() {
		restTemplate.delete(baseUrl + "/assets");
	}

	public void putAsset(int id, String name) {
		String url = baseUrl + "/assets/" + id;
		UpdateAssetRequest request = new UpdateAssetRequest();
		request.setName(name);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(request), String.class);
		log.info("PUT {} : {}", url, response.getStatusCode());
	}

	public Asset getAsset(int id) {
		String url = baseUrl + "/assets/" + id;
		ResponseEntity<Asset> response = restTemplate.getForEntity(url, Asset.class);
		log.info("GET {} : {}", url, response.getStatusCode());
		return response.getBody();
	}

	public List<Asset> getAssets() {
		Asset[] assets = restTemplate.getForObject(baseUrl + "/assets", Asset[].class);
		return Arrays.asList(assets);
	}

	public List<Asset> findAssetByName(String name) {
		Asset[] assets = restTemplate.getForObject(baseUrl + "/assets/findByName?name=" + name, Asset[].class);
		return Arrays.asList(assets);
	}

	public Map getStats() {
		return restTemplate.getForObject(baseUrl + "/cache/stats", Map.class);
	}
}
