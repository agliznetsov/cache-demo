package com.example.demo.dao;

import static com.example.demo.config.CacheNames.ASSET_FIND_BY_NAME;
import static org.hibernate.annotations.QueryHints.CACHEABLE;
import static org.hibernate.annotations.QueryHints.CACHE_REGION;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;

import com.example.demo.config.CacheNames;
import com.example.demo.model.Asset;

public interface AssetRepository extends JpaRepository<Asset,Integer> {
	@QueryHints(value = {@QueryHint(name = CACHEABLE, value = "true"), @QueryHint(name = CACHE_REGION, value = ASSET_FIND_BY_NAME)})
	List<Asset> findByName(String name);
}
