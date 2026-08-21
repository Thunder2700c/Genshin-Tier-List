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
