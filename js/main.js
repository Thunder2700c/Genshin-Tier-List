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
});

// Cursor
(function () {
  const cursor = document.querySelector('.cursor');
  const follower = document.querySelector('.cursor-follower');

  if (!cursor || !follower) return;

  let mouseX = window.innerWidth / 2;
  let mouseY = window.innerHeight / 2;
  let followerX = mouseX;
  let followerY = mouseY;

  // Update mouse position
  window.addEventListener('mousemove', (e) => {
    mouseX = e.clientX;
    mouseY = e.clientY;
  });

  // Smooth animation loop
  function render() {
    // Small dot follows instantly
    cursor.style.transform = `translate(${mouseX}px, ${mouseY}px)`;

    // Outer ring follows with lag
    followerX += (mouseX - followerX) * 0.18;
    followerY += (mouseY - followerY) * 0.18;
    follower.style.transform = `translate(${followerX}px, ${followerY}px)`;

    requestAnimationFrame(render);
  }
  render();

  // Expand on interactive elements
  const targets = document.querySelectorAll(
    'a, button, .grid-card, .char-card, .elem-btn, .tier-pill, .step-btn, .search-input, .card-ss, .protocol-card'
  );

  targets.forEach((el) => {
    el.addEventListener('mouseenter', () => follower.classList.add('active'));
    el.addEventListener('mouseleave', () => follower.classList.remove('active'));
  });
})();
