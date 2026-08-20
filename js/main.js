document.addEventListener('DOMContentLoaded', () => {
  const cards = document.querySelectorAll('.tier-card');

  // Subtle interactive tilt & focus effect
  cards.forEach(card => {
    card.addEventListener('mouseenter', () => {
      card.style.transition = 'transform 0.15s ease, border-color 0.25s ease, box-shadow 0.25s ease';
    });

    card.addEventListener('mouseleave', () => {
      card.style.transition = 'transform 0.25s ease, border-color 0.25s ease, box-shadow 0.25s ease';
    });
  });
});
