package org.huseyin.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * Created by huseyin on 24/11/2016.
 */
@Service
public class CPUService implements MetricsService {

    private GaugeService gaugeService;

    private final SystemInfo systemInfo;

    @Autowired
    public CPUService(GaugeService gaugeService){
        this.gaugeService = gaugeService;
        this.systemInfo = new SystemInfo();
        this.read();
    }

    @Override
    @Scheduled(fixedDelay = 60000)
    public void read() {
        HardwareAbstractionLayer hardware = this.systemInfo.getHardware();
        gaugeService.submit("system.cpu.load.percent", hardware.getProcessor().getSystemCpuLoad() * 100.0);
    }
}
