package com.zeroone.tenancy.event;


import com.zeroone.tenancy.model.DatasourceActionEvent;
import com.zeroone.tenancy.runner.TenancyMonitor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public class DatasourceEventListener {


    private final TenancyMonitor tenancyMonitor;


    public DatasourceEventListener(TenancyMonitor tenancyMonitor) {
        this.tenancyMonitor = tenancyMonitor;
    }

    @Async
    @EventListener
    public void datasourceEvent(DatasourceActionEvent actionEvent){
        tenancyMonitor.pushEvent(actionEvent);
    }
}
