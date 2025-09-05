import React from 'react';
import { Nav, Card, Button } from 'react-bootstrap';
import { Link } from 'react-router-dom';
import { PeopleFill, Bank2, CashStack, ArrowRight, X, ChevronLeft, ChevronRight } from 'react-bootstrap-icons';

const Sidebar = ({ isOpen, onClose, isCollapsed, onToggleCollapse }) => {
  return (
    <>
      {/* Mobile overlay */}
      {isOpen && (
        <div 
          className="d-lg-none position-fixed w-100 h-100 bg-dark opacity-50" 
          style={{ zIndex: 999, top: 0, left: 0 }}
          onClick={onClose}
        />
      )}
      
      <div className={`sidebar bg-light border-end ${isOpen ? 'show' : ''} ${isCollapsed ? 'collapsed' : ''}`} style={{ 
        width: isCollapsed ? '60px' : '250px', 
        minHeight: 'calc(100vh - 80px)', // Subtract navbar height
        position: 'fixed',
        left: 0,
        top: '80px', // Start below navbar
        zIndex: 1000,
        transition: 'width 0.3s ease'
      }}>
      <div className="p-3">
        <Card className="border shadow-sm h-100" style={{ 
          background: '#ffffff',
          borderRadius: '8px'
        }}>
          <Card.Header className="border-0 d-flex justify-content-between align-items-center py-3" style={{
            background: '#343a40',
            borderRadius: '8px 8px 0 0'
          }}>
            {!isCollapsed && (
              <h5 className="mb-0 d-flex align-items-center text-white fw-bold">
                <ArrowRight className="me-2" size={20} />
                Quick Actions
              </h5>
            )}
            <div className="d-flex align-items-center">
              <Button 
                variant="outline-light" 
                size="sm" 
                className="me-2 d-none d-lg-block rounded-pill"
                onClick={onToggleCollapse}
                style={{ 
                  border: '2px solid rgba(255,255,255,0.3)',
                  background: 'rgba(255,255,255,0.1)',
                  backdropFilter: 'blur(10px)'
                }}
              >
                {isCollapsed ? <ChevronRight size={16} /> : <ChevronLeft size={16} />}
              </Button>
              <Button 
                variant="outline-light" 
                size="sm" 
                className="d-lg-none rounded-pill"
                onClick={onClose}
                style={{ 
                  border: '2px solid rgba(255,255,255,0.3)',
                  background: 'rgba(255,255,255,0.1)',
                  backdropFilter: 'blur(10px)'
                }}
              >
                <X size={16} />
              </Button>
            </div>
          </Card.Header>
          <Card.Body className="p-4 d-flex flex-column justify-content-center" style={{ minHeight: '300px' }}>
            <Nav className="flex-column">
              <Nav.Item className="mb-3">
                <Button 
                  as={Link} 
                  to="/customers/new" 
                  className="w-100 d-flex align-items-center justify-content-start py-3 border-0" 
                  title="Add Customer"
                  style={{
                    background: '#343a40',
                    borderRadius: '8px',
                    color: 'white',
                    fontWeight: '500',
                    transition: 'all 0.2s ease'
                  }}
                  onMouseEnter={(e) => {
                    e.target.style.background = '#495057';
                    e.target.style.transform = 'translateY(-1px)';
                  }}
                  onMouseLeave={(e) => {
                    e.target.style.background = '#343a40';
                    e.target.style.transform = 'translateY(0)';
                  }}
                >
                  <PeopleFill className={isCollapsed ? "" : "me-3"} size={18} />
                  {!isCollapsed && <span>Add Customer</span>}
                </Button>
              </Nav.Item>
              <Nav.Item className="mb-3">
                <Button 
                  as={Link} 
                  to="/accounts/new" 
                  className="w-100 d-flex align-items-center justify-content-start py-3 border-0" 
                  title="Open Account"
                  style={{
                    background: '#343a40',
                    borderRadius: '8px',
                    color: 'white',
                    fontWeight: '500',
                    transition: 'all 0.2s ease'
                  }}
                  onMouseEnter={(e) => {
                    e.target.style.background = '#495057';
                    e.target.style.transform = 'translateY(-1px)';
                  }}
                  onMouseLeave={(e) => {
                    e.target.style.background = '#343a40';
                    e.target.style.transform = 'translateY(0)';
                  }}
                >
                  <Bank2 className={isCollapsed ? "" : "me-3"} size={18} />
                  {!isCollapsed && <span>Open Account</span>}
                </Button>
              </Nav.Item>
              <Nav.Item className="mb-3">
                <Button 
                  as={Link} 
                  to="/transactions/new" 
                  className="w-100 d-flex align-items-center justify-content-start py-3 border-0" 
                  title="New Transaction"
                  style={{
                    background: '#343a40',
                    borderRadius: '8px',
                    color: 'white',
                    fontWeight: '500',
                    transition: 'all 0.2s ease'
                  }}
                  onMouseEnter={(e) => {
                    e.target.style.background = '#495057';
                    e.target.style.transform = 'translateY(-1px)';
                  }}
                  onMouseLeave={(e) => {
                    e.target.style.background = '#343a40';
                    e.target.style.transform = 'translateY(0)';
                  }}
                >
                  <CashStack className={isCollapsed ? "" : "me-3"} size={18} />
                  {!isCollapsed && <span>New Transaction</span>}
                </Button>
              </Nav.Item>
            </Nav>
          </Card.Body>
        </Card>
      </div>
      </div>
    </>
  );
};

export default Sidebar;
