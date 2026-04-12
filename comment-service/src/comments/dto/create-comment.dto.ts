import { IsString, IsNotEmpty } from 'class-validator';

export class CreateCommentDto {
  @IsString()
  @IsNotEmpty()
  readonly author!: string;

  @IsString()
  @IsNotEmpty()
  readonly content!: string;

  @IsString()
  @IsNotEmpty()
  readonly bookId!: string;
}
