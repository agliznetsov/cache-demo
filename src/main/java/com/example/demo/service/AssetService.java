package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.AssetRepository;
import com.example.demo.dao.ResourceRepository;
import com.example.demo.model.Asset;
import com.example.demo.model.AssetCreatedEvent;
import com.example.demo.model.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Transactional
public class AssetService implements CommandLineRunner {
	@Autowired
	AssetRepository assetRepository;
	@Autowired
	ResourceRepository resourceRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	@Override
	public void run(String... args) throws Exception {
	}

	public void createAsset(int id, String name, String description) {
//		log.info("createAsset.start");
		Asset asset = new Asset();
		asset.setId(id);
		asset.setName(name != null ? name : "name" + id);
		asset.setDescription(description != null ? description : "description" + id);
		assetRepository.save(asset);
//		applicationEventPublisher.publishEvent(new AssetCreatedEvent(this, id));
//		log.info("createAsset.end");
	}

	public void createResource(int id, String name, String description) {
		Resource resource = new Resource();
		resource.setId(id);
		resource.setName(name != null ? name : "name" + id);
		resource.setDescription(description != null ? description : "description" + id);
		resourceRepository.saveAndFlush(resource);
	}
	public void createAsset(int id) {
		createAsset(id, null, null);
	}

	public void createAssetAndFail(int id) {
		createAsset(id, null, null);
		throw new RuntimeException("error");
	}

	public void updateAsset(int id, String newName) {
		Optional<Asset> optional = findById(id);
		if (optional.isPresent()) {
			optional.get().setName(newName);
		} else {
			Asset asset = new Asset();
			asset.setId(id);
			asset.setName(newName);
			assetRepository.saveAndFlush(asset);
		}
	}

	public void updateAssetAndFail(int id, String newName) {
		Asset asset = getAsset(id);
		asset.setName(newName);
		assetRepository.saveAndFlush(asset);
		throw new RuntimeException("error");
	}

	public Asset getAsset(int id) {
		return assetRepository.findById(id).orElseThrow(() -> new RuntimeException("asset not found"));
	}

	public void deleteAll() {
		assetRepository.deleteAll();
	}

	public Optional<Asset> findById(int id) {
		return assetRepository.findById(id);
	}

	public List<Asset> findAll() {
		return assetRepository.findAll();
	}

	public List<Asset> findAssetByName(String name) {
		return assetRepository.findByName(name);
	}

	public List<Resource> findResourceByName(String name) {
		return resourceRepository.findByName(name);
	}
}
