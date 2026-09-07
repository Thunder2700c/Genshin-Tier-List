document.addEventListener('DOMContentLoaded', () => {
  const searchInput = document.getElementById('charSearch');
  const filterButtons = document.querySelectorAll('.elem-btn');
  const charCards = document.querySelectorAll('.char-card');
  const noResults = document.getElementById('noResults');

  let activeFilter = 'all';
  let searchQuery = '';

  function filterCharacters() {
    let visibleCount = 0;

    charCards.forEach((card) => {
      const name = (card.getAttribute('data-name') || '').toLowerCase();
      const element = (card.getAttribute('data-element') || '').toLowerCase();

      const matchesSearch = !searchQuery || name.includes(searchQuery);
      const matchesElement = activeFilter === 'all' || element === activeFilter;

      if (matchesSearch && matchesElement) {
        card.style.display = 'flex';
        visibleCount++;
      } else {
        card.style.display = 'none';
      }
    });

    if (noResults) {
      noResults.style.display = visibleCount === 0 ? 'block' : 'none';
    }
  }

  // Real-time Search Listener
  if (searchInput) {
    searchInput.addEventListener('input', (e) => {
      searchQuery = e.target.value.trim().toLowerCase();
      filterCharacters();
    });
  }

  // Element Pill Filter Listener
  filterButtons.forEach((btn) => {
    btn.addEventListener('click', () => {
      filterButtons.forEach((b) => b.classList.remove('active'));
      btn.classList.add('active');

      activeFilter = (btn.getAttribute('data-filter') || 'all').toLowerCase();
      filterCharacters();
    });
  });

  // Stagger in character cards on initial load
  charCards.forEach((card, index) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(12px)';
    card.style.transition = 'opacity 0.3s ease, transform 0.3s cubic-bezier(0.16, 1, 0.3, 1)';

    setTimeout(() => {
      card.style.opacity = '1';
      card.style.transform = 'translateY(0)';
    }, 35 * index);
  });
});

// Cursor
(function () {
  const cursor = document.querySelector('.cursor');
  const follower = document.querySelector('.cursor-follower');

  if (!cursor || !follower) return;

  let mouseX = 0;
  let mouseY = 0;
  let followerX = 0;
  let followerY = 0;

  // Instant position for the small dot
  document.addEventListener('mousemove', (e) => {
    mouseX = e.clientX;
    mouseY = e.clientY;
    cursor.style.transform = `translate(${mouseX}px, ${mouseY}px)`;
  });

  // Smooth follow for the outer ring
  function animate() {
    followerX += (mouseX - followerX) * 0.15;
    followerY += (mouseY - followerY) * 0.15;
    follower.style.transform = `translate(${followerX}px, ${followerY}px)`;
    requestAnimationFrame(animate);
  }
  animate();

  // Grow on interactive elements
  const growTargets = document.querySelectorAll(
    'a, button, .grid-card, .char-card, .elem-btn, .tier-pill, .step-btn, .search-input, .card-ss, .protocol-card'
  );

  growTargets.forEach((el) => {
    el.addEventListener('mouseenter', () => follower.classList.add('active'));
    el.addEventListener('mouseleave', () => follower.classList.remove('active'));
  });
})();
