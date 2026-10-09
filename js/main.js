document.addEventListener('DOMContentLoaded', () => {
  // 1. Hardware-Accelerated GSAP Entrance Animation
  initEntranceSequence();

  // 2. Multi-Surface Spatial Glass Light Reflection Tracking
  initSpatialSpotlightEngine();

  // 3. Codegrid-Style 60-120 FPS GSAP quickTo Cursor (Preserved Unchanged)
  initSmoothCursor();
});

/**
 * Orchestrates a smooth visionOS floating entrance reveal for Bento tiles.
 */
function initEntranceSequence() {
  const cards = document.querySelectorAll('.grid-card');
  const heroElements = document.querySelectorAll('.system-bar, .hero-section > *');

  if (typeof gsap !== 'undefined') {
    // Spatial entrance with GSAP if available
    gsap.from(heroElements, {
      opacity: 0,
      y: 18,
      duration: 0.7,
      stagger: 0.08,
      ease: 'power3.out'
    });

    gsap.from(cards, {
      opacity: 0,
      y: 24,
      scale: 0.98,
      duration: 0.75,
      stagger: 0.06,
      ease: 'power3.out',
      delay: 0.2
    });
  } else {
    // Native fallback
    cards.forEach((card, index) => {
      card.style.opacity = '0';
      card.style.transform = 'translateY(16px)';
      card.style.transition = 'opacity 0.4s ease, transform 0.4s cubic-bezier(0.16, 1, 0.3, 1)';
      
      setTimeout(() => {
        card.style.opacity = '1';
        card.style.transform = 'translateY(0)';
      }, 50 * index);
    });
  }
}

/**
 * Coordinates interactive light refractions across all Bento tiles,
 * creating dynamic specular highlights on frosted glass as the pointer moves.
 */
function initSpatialSpotlightEngine() {
  const cards = document.querySelectorAll('.grid-card');

  cards.forEach((card) => {
    card.addEventListener('mousemove', (e) => {
      const rect = card.getBoundingClientRect();
      const x = ((e.clientX - rect.left) / card.clientWidth) * 100;
      const y = ((e.clientY - rect.top) / card.clientHeight) * 100;

      card.style.setProperty('--mouse-x', `${x}%`);
      card.style.setProperty('--mouse-y', `${y}%`);
    });
  });
}

/**
 * Codegrid-Style 60-120 FPS GSAP quickTo Cursor
 * PRESERVED EXACTLY AS SPECIFIED — DO NOT MODIFY
 */
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
