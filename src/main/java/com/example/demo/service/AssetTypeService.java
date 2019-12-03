package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.AssetTypeRepository;
import com.example.demo.model.AssetType;

@Component
@Transactional
public class AssetTypeService {
	@Autowired
	private AssetTypeRepository assetTypeRepository;

	public AssetType createAssetType(AssetType parent, String name) {
		AssetType assetType = new AssetType();
		assetType.setId(UUID.randomUUID().toString());
		assetType.setParentAssetType(parent);
		assetType.setName(name);
		assetTypeRepository.saveAndFlush(assetType);
		return assetType;
	}

	public synchronized List<String> getParentTypeIds(String id) {
		AssetType type = assetTypeRepository.findById(id).orElse(null);
		if (type == null) {
			return Collections.emptyList();
		}
		return new ArrayList<>(buildTypeHierarchy(type));
	}
	
	private LinkedHashSet<String> buildTypeHierarchy(AssetType type) {
		LinkedHashSet<String> parentIds = new LinkedHashSet<>();
		parentIds.add(type.getId());
		AssetType parentType = type.getParentAssetType();
		while (parentType != null && !parentIds.contains(parentType.getId())) {
			parentIds.add(parentType.getId());
			parentType = parentType.getParentAssetType();
		}
		return parentIds;
	}

	public void deleteAll() {
		assetTypeRepository.deleteAll();
	}
}
