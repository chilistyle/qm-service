import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import AuthButton from './AuthButton';
import { handleSignIn, getLogoutUrl } from "@/app/actions";

vi.mock("@/app/actions", () => ({
  handleSignIn: vi.fn(),
  getLogoutUrl: vi.fn(),
}));

describe('AuthButton Component', () => {
  
  it('should render Sign in button if session is missing', () => {
    render(<AuthButton session={null} />);
    
    const signInButton = screen.getByText(/sign in/i);
    expect(signInButton).toBeInTheDocument();
    expect(screen.queryByText(/sign out/i)).not.toBeInTheDocument();
  });

  it('should render Sign out button if the user is authenticated', () => {
    const mockSession = { user: { name: 'John Doe' } };
    render(<AuthButton session={mockSession} />);
    
    const signOutButton = screen.getByText(/sign out/i);
    expect(signOutButton).toBeInTheDocument();
    expect(screen.queryByText(/sign in/i)).not.toBeInTheDocument();
  });

  it('should call getLogoutUrl when Sign out is clicked', async () => {
    const mockSession = { user: { name: 'John Doe' } };
    render(<AuthButton session={mockSession} />);
    
    const signOutButton = screen.getByText(/sign out/i);
    await fireEvent.click(signOutButton);

    expect(getLogoutUrl).toHaveBeenCalledTimes(1);
  });
});