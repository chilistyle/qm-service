import { Injectable, OnModuleInit, OnModuleDestroy } from '@nestjs/common';
const Eureka = require('eureka-js-client').Eureka;

@Injectable()
export class EurekaService implements OnModuleInit, OnModuleDestroy {
  private client: any;

  constructor() {
    this.client = new Eureka({
      instance: {
        app: 'comment-service',
        hostName: 'comment-service',
        ipAddr: '127.0.0.1',
        port: {
          $: process.env.PORT ?? 3010,
          '@enabled': true,
        },
        vipAddress: 'comment-service',
        dataCenterInfo: {
          '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
          name: 'MyOwn',
        },
      },
      eureka: {
        host: process.env.EUREKA_HOSTNAME,
        port: process.env.EUREKA_PORT,
        servicePath: '/eureka/apps/',
      },
    });
  }

  onModuleInit() {
    this.client.start((error: any) => {
      if (error) {
        console.error('Eureka registration failed:', error);
      } else {
        console.log('Eureka registration complete');
      }
    });
  }

  onModuleDestroy() {
    this.client.stop();
  }
}
