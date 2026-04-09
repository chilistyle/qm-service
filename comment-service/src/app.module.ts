import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { CommentsModule } from './comments/comments.module';
import { MongooseModule } from '@nestjs/mongoose';
import { EurekaService } from './eureka-client.service';
import { PlayablesController } from './playables/playables.controller';

@Module({
  imports: [
    MongooseModule.forRoot(process.env.MONGO_URI || 'mongodb://localhost:27017/nestdb'),
    CommentsModule,
  ],
  controllers: [AppController, PlayablesController],
  providers: [AppService, EurekaService],
})
export class AppModule { }