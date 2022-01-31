package com.strr.licenses.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.strr.licenses.client.OrganizationRestTemplateClient;
import com.strr.licenses.config.ServiceConfig;
import com.strr.licenses.model.License;
import com.strr.licenses.model.Organization;
import com.strr.licenses.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final ServiceConfig serviceConfig;
    private final OrganizationRestTemplateClient organizationRestTemplateClient;

    @Autowired
    public LicenseService(LicenseRepository licenseRepository,
                          ServiceConfig serviceConfig,
                          OrganizationRestTemplateClient organizationRestTemplateClient) {
        this.licenseRepository = licenseRepository;
        this.serviceConfig = serviceConfig;
        this.organizationRestTemplateClient = organizationRestTemplateClient;
    }

    private Organization getOrganization(String organizationId) {
        return organizationRestTemplateClient.getOrganization(organizationId);
    }

    public License getLicense(String organizationId, String licenseId) {
        License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
        Organization org = getOrganization(organizationId);
        return license
                .withOrganizationName( org.getName())
                .withContactName( org.getContactName())
                .withContactEmail( org.getContactEmail() )
                .withContactPhone( org.getContactPhone() )
                .withComment(serviceConfig.getExampleProperty());
    }

    private void randomlyRunLong() {
        Random random = new Random();
        int randomNum = random.nextInt(3) + 1;
        if (randomNum == 3) {
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(11000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @HystrixCommand(
            commandProperties = {
                    // 设置超时
                    // @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "12000")
            },
            // 设置回退
            fallbackMethod = "buildFallbackLicenseList",
            // 线程池名称
            threadPoolKey = "licenseByOrgThreadPool",
            threadPoolProperties = {
                    // 核心线程数
                    @HystrixProperty(name = "coreSize", value = "30"),
                    // 请求队列
                    @HystrixProperty(name = "maxQueueSize", value = "10")
            }
    )
    public List<License> getLicensesByOrg(String organizationId) {
        randomlyRunLong();
        return licenseRepository.findByOrganizationId(organizationId);
    }

    // 回退
    private List<License> buildFallbackLicenseList(String organizationId) {
        List<License> fallbackList = new ArrayList<>();
        License license = new License()
                .withId("0000000-00-00000")
                .withOrganizationId( organizationId )
                .withProductName("Sorry no licensing information currently available");

        fallbackList.add(license);
        return fallbackList;
    }

    public void saveLicense(License license) {
        license.withId( UUID.randomUUID().toString());
        licenseRepository.save(license);
    }

    public void updateLicense(License license) {
        licenseRepository.save(license);
    }

    public void deleteLicense(String licenseId) {
        licenseRepository.deleteById(licenseId);
    }
}
