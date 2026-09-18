document.addEventListener('DOMContentLoaded', () => {
  // Stagger reveal animation for cards
  const cards = document.querySelectorAll('.grid-card');
  
  cards.forEach((card, index) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(12px)';
    card.style.transition = 'opacity 0.35s ease, transform 0.35s cubic-bezier(0.16, 1, 0.3, 1)';
    
    setTimeout(() => {
      card.style.opacity = '1';
      card.style.transform = 'translateY(0)';
    }, 45 * index);
  });

  // Codegrid-Style 60-120 FPS GSAP quickTo Cursor
  initSmoothCursor();
});

function initSmoothCursor() {
  const cursor = document.querySelector('.cursor');
  const follower = document.querySelector('.cursor-follower');

  // Verify elements exist and user is on a desktop pointer device
  if (!cursor || !follower || !window.matchMedia('(hover: hover) and (pointer: fine)').matches) {
    return;
  }

  // Check if GSAP is available
  if (typeof gsap === 'undefined') {
    console.warn('GSAP is required for smooth cursor rendering.');
    return;
  }

  // Pre-center both elements using GPU percent transforms
  gsap.set([cursor, follower], {
    xPercent: -50,
    yPercent: -50,
    opacity: 0
  });

  // quickTo pipes coordinates directly to GPU translate3d with zero CPU layout thrashing
  // Dot: near-instant response (0.08s)
  const xDot = gsap.quickTo(cursor, 'x', { duration: 0.08, ease: 'power3.out' });
  const yDot = gsap.quickTo(cursor, 'y', { duration: 0.08, ease: 'power3.out' });

  // Follower Ring: fluid organic easing (0.35s)
  const xFollower = gsap.quickTo(follower, 'x', { duration: 0.35, ease: 'power3.out' });
  const yFollower = gsap.quickTo(follower, 'y', { duration: 0.35, ease: 'power3.out' });

  let isVisible = false;

  window.addEventListener('mousemove', (e) => {
    const { clientX, clientY } = e;

    if (!isVisible) {
      gsap.to([cursor, follower], { opacity: 1, duration: 0.25, ease: 'power2.out' });
      isVisible = true;
    }

    xDot(clientX);
    yDot(clientY);
    xFollower(clientX);
    yFollower(clientY);
  });

  window.addEventListener('mouseleave', () => {
    gsap.to([cursor, follower], { opacity: 0, duration: 0.25, ease: 'power2.out' });
    isVisible = false;
  });

  // Interactive Target Expansion using GPU scale instead of width/height
  const interactiveTargets = document.querySelectorAll(
    'a, button, .grid-card, .char-card, .elem-btn, .tier-pill, .step-btn, .search-input, .card-ss, .protocol-card'
  );

  interactiveTargets.forEach((target) => {
    target.addEventListener('mouseenter', () => {
      gsap.to(follower, {
        scale: 1.55,
        borderColor: '#e8c87a',
        backgroundColor: 'rgba(232, 200, 122, 0.08)',
        duration: 0.3,
        ease: 'power2.out',
        overwrite: 'auto'
      });
      gsap.to(cursor, {
        scale: 0.6,
        duration: 0.3,
        ease: 'power2.out',
        overwrite: 'auto'
      });
    });

    target.addEventListener('mouseleave', () => {
      gsap.to(follower, {
        scale: 1,
        borderColor: 'rgba(232, 200, 122, 0.65)',
        backgroundColor: 'transparent',
        duration: 0.3,
        ease: 'power2.out',
        overwrite: 'auto'
      });
      gsap.to(cursor, {
        scale: 1,
        duration: 0.3,
        ease: 'power2.out',
        overwrite: 'auto'
      });
    });
  });
}
