package org.huseyin.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

/**
 * Created by huseyin on 25/11/2016.
 */
@Service
public class MemoryService implements MetricsService {
    private GaugeService gaugeService;

    private final SystemInfo systemInfo;

    @Autowired
    public MemoryService(GaugeService gaugeService){
        this.gaugeService = gaugeService;
        this.systemInfo = new SystemInfo();
        this.read();
    }

    @Override
    @Scheduled(fixedDelay = 60000)
    public void read() {
        HardwareAbstractionLayer hardware = this.systemInfo.getHardware();
        double total = (double)hardware.getMemory().getTotal();
        double free = (double)hardware.getMemory().getAvailable();
        double used = total - free;
        gaugeService.submit("system.memory.free.bytes", free);
        gaugeService.submit("system.memory.used.bytes", used);
        gaugeService.submit("system.memory.used.percent", (1.0 - (free / total)) * 100.0);
    }
}
