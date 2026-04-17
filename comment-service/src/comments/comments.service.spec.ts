import { Test, TestingModule } from '@nestjs/testing';
import { CommentsService } from './comments.service';
import { getModelToken } from '@nestjs/mongoose';
import { Comment } from './schemas/comment.schema';

describe('CommentsService', () => {
  let service: CommentsService;
  let model: any;

  const mockComment = {
    _id: '123',
    content: 'Test content',
    bookId: 'book_1',
    save: jest.fn(),
  };

  function mockCommentModel(this: any, dto: any) {
    this.data = dto;
    this.save = mockComment.save.mockResolvedValue({ ...dto, _id: '123' });
  }

  mockCommentModel.find = jest.fn().mockReturnThis();
  mockCommentModel.exec = jest.fn();

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        CommentsService,
        {
          provide: getModelToken(Comment.name),
          useValue: mockCommentModel,
        },
      ],
    }).compile();

    service = module.get<CommentsService>(CommentsService);
    model = module.get(getModelToken(Comment.name));
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('create', () => {
    it('should insert a new comment', async () => {
      const dto = { content: 'New comment', bookId: '123' };

      const result = await service.create(dto as any);

      expect(result).toBeDefined();
      expect(result._id).toEqual('123');
      expect(mockComment.save).toHaveBeenCalled();
    });
  });

  describe('findAll', () => {
    it('should return all comments', async () => {
      const commentsArray = [{ content: 'Comment 1' }];

      model.find.mockReturnValue({
        exec: jest.fn().mockResolvedValue(commentsArray),
      });

      const result = await service.findAll();
      expect(result).toEqual(commentsArray);
    });
  });
});