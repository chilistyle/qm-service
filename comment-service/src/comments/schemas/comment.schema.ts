import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { Document } from 'mongoose';

@Schema({ timestamps: true })
export class Comment extends Document {
  @Prop({ required: true })
  author!: string;

  @Prop({ required: true })
  content!: string;

  @Prop({ required: true })
  bookId!: string;
}

export const CommentSchema = SchemaFactory.createForClass(Comment);
