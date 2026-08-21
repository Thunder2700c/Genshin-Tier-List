document.addEventListener('DOMContentLoaded', () => {
  // Stagger in the bento cards on load
  const cards = document.querySelectorAll('.grid-card');
  
  cards.forEach((card, index) => {
    card.style.opacity = '0';
    card.style.transform = 'translateY(15px)';
    card.style.transition = 'opacity 0.4s ease, transform 0.4s cubic-bezier(0.16, 1, 0.3, 1)';
    
    setTimeout(() => {
      card.style.opacity = '1';
      card.style.transform = 'translateY(0)';
    }, 60 * index);
  });
});
