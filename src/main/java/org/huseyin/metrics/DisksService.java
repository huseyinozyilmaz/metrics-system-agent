package org.huseyin.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by huseyin on 25/11/2016.
 */
@Service
public class DisksService implements MetricsService {

    private GaugeService gaugeService;

    @Autowired
    public DisksService(GaugeService gaugeService){
        this.gaugeService = gaugeService;
        this.read();
    }

    @Override
    @Scheduled(fixedDelay = 60000)
    public void read() {
        File[] drives = File.listRoots();
        if (drives != null && drives.length > 0) {
            long free;
            long used;
            for (File aDrive : drives) {
                free = aDrive.getFreeSpace();
                used = aDrive.getTotalSpace() - free;
                gaugeService.submit(buildGaugeKey(aDrive.toString(), "free.bytes"), free);
                gaugeService.submit(buildGaugeKey(aDrive.toString(), "used.bytes"), used);
                gaugeService.submit(buildGaugeKey(aDrive.toString(), "used.percent"),
                        (1.0-((double)free / (double)aDrive.getTotalSpace()))*100.0);
            }
        }
    }

    private String buildGaugeKey(String aDrive, String metric) {
        return String.format("system.disks.%s.%s", normaliseDriveName(aDrive), metric);
    }

    private String normaliseDriveName(String aDrive){
        String driveName;
        if(aDrive.toString().equals("/") || aDrive.toString().startsWith("/")) {
            driveName = "root";
        }
        else {
            driveName = String.valueOf(aDrive.toString().charAt(0));
        }
        return driveName;
    }
}
