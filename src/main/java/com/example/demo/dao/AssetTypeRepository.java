package com.example.demo.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.AssetType;

public interface AssetTypeRepository extends JpaRepository<AssetType,String> {
}
