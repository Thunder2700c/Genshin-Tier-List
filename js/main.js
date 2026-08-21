/* ==========================================================================
   CODEGRID GSAP ENGINE (Hover Image Reveal, Marquee, & Load Timelines)
   ========================================================================== */

document.addEventListener('DOMContentLoaded', () => {
  gsap.registerPlugin(ScrollTrigger);

  // 1. Hero Load Stagger Timeline
  const heroTL = gsap.timeline({ defaults: { ease: 'power4.out', duration: 1.2 } });

  heroTL
    .from('.telemetry-bar', { opacity: 0, y: -20 })
    .from('.agency-badge', { opacity: 0, y: 20, stagger: 0.1 }, '-=0.8')
    .from('.hero-char', { y: '110%', stagger: 0.08, duration: 1.4 }, '-=0.9')
    .from('.hero-lead, .hero-cta-group', { opacity: 0, y: 30, stagger: 0.15 }, '-=0.8');

  // 2. Infinite Continuous Marquee Ticker
  const ticker = document.querySelector('.ticker-move');
  if (ticker) {
    gsap.to(ticker, {
      xPercent: -50,
      repeat: -1,
      duration: 22,
      ease: 'none'
    });
  }

  // 3. Codegrid Signature Interactive Image Reveal (Cursor Tracker)
  const cursor = document.getElementById('cursorFollower');
  const cursorImg = document.getElementById('cursorImg');
  const cursorTier = document.getElementById('cursorTier');
  const cursorName = document.getElementById('cursorName');
  const rows = document.querySelectorAll('.index-row');

  // Smooth position tracking using gsap.quickTo
  const setX = gsap.quickTo(cursor, 'x', { duration: 0.35, ease: 'power3' });
  const setY = gsap.quickTo(cursor, 'y', { duration: 0.35, ease: 'power3' });

  window.addEventListener('mousemove', (e) => {
    setX(e.clientX);
    setY(e.clientY);
  });

  rows.forEach((row) => {
    row.addEventListener('mouseenter', () => {
      const imgSrc = row.getAttribute('data-img');
      const tierText = row.getAttribute('data-tier');
      const nameText = row.getAttribute('data-name');

      if (imgSrc && cursorImg) cursorImg.src = imgSrc;
      if (tierText && cursorTier) cursorTier.textContent = tierText;
      if (nameText && cursorName) cursorName.textContent = nameText;

      gsap.to(cursor, {
        opacity: 1,
        visibility: 'visible',
        scale: 1,
        duration: 0.35,
        ease: 'power3.out'
      });
    });

    row.addEventListener('mouseleave', () => {
      gsap.to(cursor, {
        opacity: 0,
        scale: 0.85,
        duration: 0.25,
        ease: 'power3.in',
        onComplete: () => {
          gsap.set(cursor, { visibility: 'hidden' });
        }
      });
    });
  });

  // 4. ScrollTrigger Reveals for Manifesto Grid
  gsap.from('.manifesto-card', {
    scrollTrigger: {
      trigger: '.manifesto-grid',
      start: 'top 80%',
    },
    y: 50,
    opacity: 0,
    stagger: 0.15,
    duration: 1,
    ease: 'power3.out'
  });
});
