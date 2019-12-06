package com.example.demo.model;

import static com.example.demo.config.CacheNames.ASSET;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.example.demo.config.CacheNames;

import lombok.Data;

@Data
@Entity
@Table(indexes = {@Index(name = "ix_asset_name", unique = true, columnList = "name")})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = ASSET)
public class Asset {
	@Id
	private Integer id;

	private String name;

	private String description;

	private long time;

	private String name2;
	private String name3;
	private String name4;
	private String name5;
}
