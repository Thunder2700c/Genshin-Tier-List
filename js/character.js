document.addEventListener('DOMContentLoaded', () => {
  // Smooth interactive reveal for cards on scroll
  const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
  };

  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.style.opacity = '1';
        entry.target.style.transform = 'translateY(0)';
        observer.unobserve(entry.target);
      }
    });
  }, observerOptions);

  document.querySelectorAll('.section-block').forEach(block => {
    block.style.opacity = '0';
    block.style.transform = 'translateY(15px)';
    block.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
    observer.observe(block);
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

  window.addEventListener('mousemove', (e) => {
    mouseX = e.clientX;
    mouseY = e.clientY;
  });

  function render() {
    // Center the small dot exactly on the mouse
    cursor.style.left = mouseX + 'px';
    cursor.style.top  = mouseY + 'px';
    cursor.style.transform = 'translate(-50%, -50%)';

    // Smooth follow for the ring
    followerX += (mouseX - followerX) * 0.18;
    followerY += (mouseY - followerY) * 0.18;
    follower.style.left = followerX + 'px';
    follower.style.top  = followerY + 'px';
    follower.style.transform = 'translate(-50%, -50%)';

    requestAnimationFrame(render);
  }
  render();

  const targets = document.querySelectorAll(
    'a, button, .grid-card, .char-card, .elem-btn, .tier-pill, .step-btn, .search-input, .card-ss, .protocol-card'
  );

  targets.forEach((el) => {
    el.addEventListener('mouseenter', () => follower.classList.add('active'));
    el.addEventListener('mouseleave', () => follower.classList.remove('active'));
  });
})();
