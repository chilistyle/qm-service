import { Test, TestingModule } from '@nestjs/testing';
import { EurekaService } from './eureka-client.service';

const mockStart = jest.fn((callback) => callback(null));
const mockStop = jest.fn();



jest.mock('eureka-js-client', () => {
    return {
        Eureka: jest.fn().mockImplementation(() => {
            return {
                start: mockStart,
                stop: mockStop,
            };
        }),
    };
});

const { Eureka } = require('eureka-js-client');

describe('EurekaService', () => {
    let service: EurekaService;

    beforeAll(() => {
        process.env.PORT = '3010';
        process.env.EUREKA_HOSTNAME = 'localhost';
        process.env.EUREKA_PORT = '8761';
    });

    beforeEach(async () => {
        jest.clearAllMocks();

        const module: TestingModule = await Test.createTestingModule({
            providers: [EurekaService],
        }).compile();

        service = module.get<EurekaService>(EurekaService);
    });

    it('should be defined', () => {
        expect(service).toBeDefined();
    });

    it('should initialize Eureka client with correct config in constructor', () => {
        expect(Eureka).toHaveBeenCalledWith(expect.objectContaining({
            instance: expect.objectContaining({
                app: 'comment-service',
            }),
        }));
    });

    describe('onModuleInit', () => {
        it('should call client.start()', () => {
            service.onModuleInit();
            expect(mockStart).toHaveBeenCalled();
        });

        it('should log error if start fails', () => {
            const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
            mockStart.mockImplementationOnce((callback) => callback(new Error('Fail')));

            service.onModuleInit();

            expect(consoleSpy).toHaveBeenCalledWith(
                'Eureka registration failed:',
                expect.any(Error)
            );
            consoleSpy.mockRestore();
        });
    });

    describe('onModuleDestroy', () => {
        it('should call client.stop()', () => {
            service.onModuleDestroy();
            expect(mockStop).toHaveBeenCalled();
        });
    });
});