import { Test, TestingModule } from '@nestjs/testing';
import { PlayablesController } from './playables.controller';

describe('PlayablesController', () => {
  let controller: PlayablesController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [PlayablesController],
    }).compile();

    controller = module.get<PlayablesController>(PlayablesController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });
});
