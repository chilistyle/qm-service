import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import AuthButton from './AuthButton';
import { handleSignIn, getLogoutUrl } from "@/app/actions";

vi.mock("@/app/actions", () => ({
  handleSignIn: vi.fn(),
  getLogoutUrl: vi.fn(),
}));

describe('AuthButton Component', () => {
  
  it('повинен рендерити кнопку Sign in, якщо сесія відсутня', () => {
    render(<AuthButton session={null} />);
    
    const signInButton = screen.getByText(/sign in/i);
    expect(signInButton).toBeInTheDocument();
    expect(screen.queryByText(/sign out/i)).not.toBeInTheDocument();
  });

  it('повинен рендерити кнопку Sign out, якщо користувач авторизований', () => {
    const mockSession = { user: { name: 'John Doe' } };
    render(<AuthButton session={mockSession} />);
    
    const signOutButton = screen.getByText(/sign out/i);
    expect(signOutButton).toBeInTheDocument();
    expect(screen.queryByText(/sign in/i)).not.toBeInTheDocument();
  });

  it('повинен викликати getLogoutUrl при натисканні на Sign out', async () => {
    const mockSession = { user: { name: 'John Doe' } };
    render(<AuthButton session={mockSession} />);
    
    const signOutButton = screen.getByText(/sign out/i);
    await fireEvent.click(signOutButton);

    expect(getLogoutUrl).toHaveBeenCalledTimes(1);
  });
});