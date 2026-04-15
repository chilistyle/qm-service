import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import Header from './Header';

vi.mock("@/components/AuthButton", () => ({
    default: ({ session }: { session: any }) => (
        <div data-testid="auth-button">
            {session ? "Authenticated" : "Guest"}
        </div>
    ),
}));

describe('Header Component', () => {
    const mockSession = { user: { name: 'John Doe' } };

    it('повинен відображати логотип та основні посилання', () => {
        render(<Header session={null} />);

        expect(screen.getByText(/QM/i)).toBeInTheDocument();
        expect(screen.getByText(/Service/i)).toBeInTheDocument();

        const historyLinks = screen.getAllByText('History');
        const settingsLinks = screen.getAllByText('Settings');

        expect(historyLinks).toHaveLength(2);
        expect(settingsLinks).toHaveLength(2);

        expect(historyLinks[0]).toBeInTheDocument();
    });

    it('повинен передавати сесію в AuthButton', () => {
        render(<Header session={mockSession} />);

        const authButtons = screen.getAllByTestId('auth-button');

        expect(authButtons).toHaveLength(2);

        expect(authButtons[0]).toHaveTextContent('Authenticated');
    });

    describe('Mobile Menu', () => {
        it('повинен бути закритим за замовчуванням', () => {
            render(<Header session={null} />);
            const mobileMenu = screen.getByText('History', { selector: '.md\\:hidden .block' }).closest('div.md\\:hidden');
            expect(mobileMenu).toHaveClass('hidden');
        });

        it('повинен відкриватися при кліку на бургер-кнопку', async () => {
            render(<Header session={null} />);

            const burgerButton = screen.getByRole('button', { name: /open main menu/i });
            await fireEvent.click(burgerButton);

            const mobileLinks = screen.getAllByText('History');
            const visibleMobileLink = mobileLinks.find(link =>
                link.closest('.md\\:hidden')
            );

            expect(visibleMobileLink?.closest('div.md\\:hidden')).toHaveClass('block');
        });

        it('повинен закриватися після кліку на посилання в мобільному меню', async () => {
            render(<Header session={null} />);

            const burgerButton = screen.getByRole('button', { name: /open main menu/i });
            await fireEvent.click(burgerButton);

            const mobileLink = screen.getAllByText('History').find(el => el.closest('.md\\:hidden'));
            await fireEvent.click(mobileLink!);

            const mobileMenu = screen.getByText('History', { selector: '.md\\:hidden .block' }).closest('div.md\\:hidden');
            expect(mobileMenu).toHaveClass('hidden');
        });
    });
});