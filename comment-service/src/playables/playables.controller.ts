// src/playables/playables.controller.ts
import { Controller, Get, Render, Query } from '@nestjs/common';

@Controller('playables')
export class PlayablesController {
    @Get('hc')
    getHealth(): string {
        return "UP";
    }

    @Get('preview')
    @Render('playable') // Nest шукатиме views/playable.ejs
    getPlayable(@Query('id') id: string) {
        return {
            campaignName: 'Злови подарунок!',
            targetScore: 5,
            storeUrl: 'https://example.com',
            correlationId: 'req-123-abc' // передаємо наш ID для трекінгу
        };
    }

    @Get('3d')
    @Render('playable-3d')
    getPlayable3d() {
        return {
            campaignName: '3D Mystery Box',
            storeUrl: 'https://example.com',
        };
    }

    @Get('3d-physics')
    @Render('playable-3d-physics')
    getPlayable3dPhysics() {
        return {
            campaignName: '3D Mystery Box',
            storeUrl: 'https://example.com',
        };
    }

    @Get('ammo')
    @Render('playable-3d-ammo')
    getAmmoPlayable() {
        return { storeUrl: 'https://example.com' };
    }

    @Get('ammo2')
    @Render('playable-3d-ammo2')
    getAmmo2Playable() {
        return { storeUrl: 'https://example.com' };
    }
}