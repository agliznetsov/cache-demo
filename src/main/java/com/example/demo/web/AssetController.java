package com.example.demo.web;

import java.util.List;
import java.util.Optional;

import javax.persistence.Cacheable;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.AssetRepository;
import com.example.demo.model.Asset;
import com.example.demo.service.AssetService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/assets")
public class AssetController {

	@Autowired
	AssetService assetService;

	@GetMapping("/{id}")
	public Optional<Asset> getAsset(@PathVariable Integer id) {
		log.info("get Asset {}", id);
		return assetService.findById(id);
	}

	@PutMapping("/{id}")
	public void updateAsset(@PathVariable Integer id, @RequestBody UpdateAssetRequest request) {
		log.info("put Asset {} = {}", id, request);
		assetService.updateAsset(id, request.getName());
	}

	@GetMapping("")
	public List<Asset> getAssets() {
		log.info("get Assets");
		return assetService.findAll();
	}

	@DeleteMapping("")
	public void deleteAssets() {
		log.info("delete Assets");
		assetService.deleteAll();
	}

	@GetMapping("/findByName")
	public List<Asset> getAssets(@RequestParam(name = "name") String name) {
		log.info("findAssetByName, name = {}", name);
		return assetService.findAssetByName(name);
	}

}
