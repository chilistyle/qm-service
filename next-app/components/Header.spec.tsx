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

    it('should display the logo and main links', () => {
        render(<Header session={null} />);

        expect(screen.getByText(/QM/i)).toBeInTheDocument();
        expect(screen.getByText(/Service/i)).toBeInTheDocument();

        const historyLinks = screen.getAllByText('History');
        const settingsLinks = screen.getAllByText('Settings');

        expect(historyLinks).toHaveLength(2);
        expect(settingsLinks).toHaveLength(2);

        expect(historyLinks[0]).toBeInTheDocument();
    });

    it('should pass the session to AuthButton', () => {
        render(<Header session={mockSession} />);

        const authButtons = screen.getAllByTestId('auth-button');

        expect(authButtons).toHaveLength(2);

        expect(authButtons[0]).toHaveTextContent('Authenticated');
    });

    describe('Mobile Menu', () => {
        it('should be closed by default', () => {
            render(<Header session={null} />);
            const mobileMenu = screen.getByText('History', { selector: '.md\\:hidden .block' }).closest('div.md\\:hidden');
            expect(mobileMenu).toHaveClass('hidden');
        });

        it('should open when the burger button is clicked', async () => {
            render(<Header session={null} />);

            const burgerButton = screen.getByRole('button', { name: /open main menu/i });
            await fireEvent.click(burgerButton);

            const mobileLinks = screen.getAllByText('History');
            const visibleMobileLink = mobileLinks.find(link =>
                link.closest('.md\\:hidden')
            );

            expect(visibleMobileLink?.closest('div.md\\:hidden')).toHaveClass('block');
        });

        it('should close after clicking a link in the mobile menu', async () => {
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