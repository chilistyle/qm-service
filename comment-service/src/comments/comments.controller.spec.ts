import { Test, TestingModule } from '@nestjs/testing';
import { CommentsController } from './comments.controller';
import { CommentsService } from './comments.service';
import { CreateCommentDto } from './dto/create-comment.dto';
import { UpdateCommentDto } from './dto/update-comment.dto';

describe('CommentsController', () => {
  let controller: CommentsController;
  let service: CommentsService;

  const mockCommentsService = {
    create: jest.fn((dto: CreateCommentDto) => {
      return { id: 1, ...dto };
    }),
    findAll: jest.fn(() => {
      return [{ id: 1, content: 'Test comment' }];
    }),
    findOne: jest.fn((id: number) => {
      return { id, content: `This action returns a #${id} comment` };
    }),
    update: jest.fn((id: number, dto: UpdateCommentDto) => {
      return { id, ...dto };
    }),
    remove: jest.fn((id: number) => {
      return { deleted: true, id };
    }),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [CommentsController],
      providers: [
        {
          provide: CommentsService,
          useValue: mockCommentsService,
        },
      ],
    }).compile();

    controller = module.get<CommentsController>(CommentsController);
    service = module.get<CommentsService>(CommentsService);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('create()', () => {
    it('should call service.create and return result', async () => {
      const dto: CreateCommentDto = { content: 'New comment', bookId: '123' } as any;
      const result = await controller.create(dto);

      expect(result).toEqual({ id: 1, ...dto });
      expect(service.create).toHaveBeenCalledWith(dto);
    });
  });

  describe('findAll()', () => {
    it('should call service.findAll', async () => {
      const result = await controller.findAll();
      expect(result).toEqual([{ id: 1, content: 'Test comment' }]);
      expect(service.findAll).toHaveBeenCalled();
    });
  });

  describe('findOne()', () => {
    it('should return a single comment', async () => {
      const id = '10';
      const result = await controller.findOne(id);

      expect(service.findOne).toHaveBeenCalledWith(10);
      expect(result).toHaveProperty('id', 10);
    });
  });

  describe('update()', () => {
    it('should call service.update with id and dto', async () => {
      const id = '1';
      const dto: UpdateCommentDto = { content: 'Updated content' };

      await controller.update(id, dto);
      expect(service.update).toHaveBeenCalledWith(1, dto);
    });
  });

  describe('remove()', () => {
    it('should call service.remove', async () => {
      const id = '1';
      await controller.remove(id);
      expect(service.remove).toHaveBeenCalledWith(1);
    });
  });
});