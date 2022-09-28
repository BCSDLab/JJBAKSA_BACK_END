package com.jjbacsa.jjbacsabackend.scrap.controller;

import com.jjbacsa.jjbacsabackend.etc.annotations.Auth;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapDirectoryResponse;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapRequest;
import com.jjbacsa.jjbacsabackend.scrap.dto.ScrapResponse;
import com.jjbacsa.jjbacsabackend.scrap.service.ScrapService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class ScrapController {

    private final ScrapService service;

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/scraps/directories")
    public ResponseEntity<ScrapDirectoryResponse> createDirectory(
            @RequestBody ScrapDirectoryRequest request) throws Exception {

        return new ResponseEntity<>(service.createDirectory(request), HttpStatus.CREATED);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/scraps/directories")
    public ResponseEntity<Page<ScrapDirectoryResponse>> getDirectories(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws Exception {

        return new ResponseEntity<>(service.getDirectories(cursor, pageSize), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value = "/scraps/directories/{directory_id}")
    public ResponseEntity<ScrapDirectoryResponse> updateDirectory(
            @PathVariable("directory_id") Long directoryId,
            @RequestBody ScrapDirectoryRequest request) throws Exception {

        return new ResponseEntity<>(service.updateDirectory(directoryId, request), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @DeleteMapping(value = "/scrap/directories/{directory_id}")
    public ResponseEntity<Void> deleteDirectory(
            @PathVariable("directory_id") Long directoryId) throws Exception {

        service.deleteDirectory(directoryId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PostMapping(value = "/scraps")
    public ResponseEntity<ScrapResponse> create(
            @RequestBody ScrapRequest request) throws Exception {

        return new ResponseEntity<>(service.create(request), HttpStatus.CREATED);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @GetMapping(value = "/scraps/directories/{directory_id}")
    public ResponseEntity<Page<ScrapResponse>> getScraps(
            @PathVariable("directory_id") Long directoryId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) throws Exception {

        return new ResponseEntity<>(service.getScraps(directoryId, cursor, pageSize), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @PatchMapping(value = "/scraps/{scrap_id}")
    public ResponseEntity<ScrapResponse> move(
            @PathVariable("scrap_id") Long scrapId,
            @RequestBody ScrapRequest request) throws Exception {

        return new ResponseEntity<>(service.move(scrapId, request), HttpStatus.OK);
    }

    @Auth
    @ApiOperation(value = "", notes = "", authorizations = @Authorization(value = "Bearer +accessToken"))
    @DeleteMapping(value = "/scraps/{scrap_id}")
    public ResponseEntity<Void> delete(
            @PathVariable("scrap_id") Long scrapId) throws Exception {

        service.delete(scrapId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
