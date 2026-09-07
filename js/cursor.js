const cursor = document.querySelector('.cursor');
const follower = document.querySelector('.cursor-follower');

if (cursor && follower) {
  let mouseX = 0, mouseY = 0;
  let followerX = 0, followerY = 0;

  document.addEventListener('mousemove', (e) => {
    mouseX = e.clientX;
    mouseY = e.clientY;
    cursor.style.transform = `translate(${mouseX}px, ${mouseY}px)`;
  });

  function animateFollower() {
    followerX += (mouseX - followerX) * 0.15;
    followerY += (mouseY - followerY) * 0.15;
    follower.style.transform = `translate(${followerX}px, ${followerY}px)`;
    requestAnimationFrame(animateFollower);
  }
  animateFollower();

  // Grow on interactive elements
  const interactives = document.querySelectorAll('a, button, .grid-card, .char-card, .elem-btn, .tier-pill, .step-btn');
  interactives.forEach(el => {
    el.addEventListener('mouseenter', () => follower.classList.add('active'));
    el.addEventListener('mouseleave', () => follower.classList.remove('active'));
  });
}
